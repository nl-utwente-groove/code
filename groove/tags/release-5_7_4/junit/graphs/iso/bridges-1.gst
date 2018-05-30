<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="take-away-1">
        <node id="n10">
            <attr name="layout">
                <string>346 132 59 46</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>233 110 48 46</string>
            </attr>
        </node>
        <node id="n11">
            <attr name="layout">
                <string>52 110 50 46</string>
            </attr>
        </node>
        <node id="n7">
            <attr name="layout">
                <string>153 170 59 46</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>294 60 59 46</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>261 5 48 46</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>484 132 48 46</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>280 226 48 46</string>
            </attr>
        </node>
        <edge to="n3" from="n10">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n1" from="n10">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n10" from="n10">
            <attr name="label">
                <string>type:Bridge</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:Area</string>
            </attr>
        </edge>
        <edge to="n11" from="n11">
            <attr name="label">
                <string>type:Euler</string>
            </attr>
        </edge>
        <edge to="n1" from="n11">
            <attr name="label">
                <string>here</string>
            </attr>
        </edge>
        <edge to="n1" from="n7">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n2" from="n7">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>type:Bridge</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:Bridge</string>
            </attr>
        </edge>
        <edge to="n1" from="n5">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n0" from="n5">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:Area</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:Area</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:Area</string>
            </attr>
        </edge>
    </graph>
</gxl>
