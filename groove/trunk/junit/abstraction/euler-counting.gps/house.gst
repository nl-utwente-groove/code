<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="house">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n7">
            <attr name="layout">
                <string>75 131 48 46</string>
            </attr>
        </node>
        <node id="n9">
            <attr name="layout">
                <string>75 286 48 46</string>
            </attr>
        </node>
        <node id="n2">
            <attr name="layout">
                <string>305 212 59 31</string>
            </attr>
        </node>
        <node id="n0">
            <attr name="layout">
                <string>310 285 48 46</string>
            </attr>
        </node>
        <node id="n1">
            <attr name="layout">
                <string>49 24 51 46</string>
            </attr>
        </node>
        <node id="n3">
            <attr name="layout">
                <string>311 132 48 46</string>
            </attr>
        </node>
        <node id="n13">
            <attr name="layout">
                <string>186 295 59 31</string>
            </attr>
        </node>
        <node id="n6">
            <attr name="layout">
                <string>124 82 59 31</string>
            </attr>
        </node>
        <node id="n12">
            <attr name="layout">
                <string>159 198 59 31</string>
            </attr>
        </node>
        <node id="n10">
            <attr name="layout">
                <string>218 198 59 31</string>
            </attr>
        </node>
        <node id="n11">
            <attr name="layout">
                <string>184 143 59 31</string>
            </attr>
        </node>
        <node id="n4">
            <attr name="layout">
                <string>245 82 59 31</string>
            </attr>
        </node>
        <node id="n8">
            <attr name="layout">
                <string>72 217 59 31</string>
            </attr>
        </node>
        <node id="n5">
            <attr name="layout">
                <string>186 25 48 31</string>
            </attr>
        </node>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>type:Area</string>
            </attr>
        </edge>
        <edge to="n7" from="n7">
            <attr name="label">
                <string>even</string>
            </attr>
        </edge>
        <edge to="n9" from="n9">
            <attr name="label">
                <string>odd</string>
            </attr>
        </edge>
        <edge to="n9" from="n9">
            <attr name="label">
                <string>type:Area</string>
            </attr>
        </edge>
        <edge to="n3" from="n2">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n0" from="n2">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n2" from="n2">
            <attr name="label">
                <string>type:Bridge</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>type:Area</string>
            </attr>
        </edge>
        <edge to="n0" from="n0">
            <attr name="label">
                <string>odd</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>type:Euler</string>
            </attr>
        </edge>
        <edge to="n1" from="n1">
            <attr name="label">
                <string>home</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>type:Area</string>
            </attr>
        </edge>
        <edge to="n3" from="n3">
            <attr name="label">
                <string>even</string>
            </attr>
        </edge>
        <edge to="n0" from="n13">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n9" from="n13">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n13" from="n13">
            <attr name="label">
                <string>type:Bridge</string>
            </attr>
        </edge>
        <edge to="n6" from="n6">
            <attr name="label">
                <string>type:Bridge</string>
            </attr>
        </edge>
        <edge to="n7" from="n6">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n5" from="n6">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n12" from="n12">
            <attr name="label">
                <string>type:Bridge</string>
            </attr>
        </edge>
        <edge to="n0" from="n12">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n7" from="n12">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n3" from="n10">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n10" from="n10">
            <attr name="label">
                <string>type:Bridge</string>
            </attr>
        </edge>
        <edge to="n9" from="n10">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n11" from="n11">
            <attr name="label">
                <string>type:Bridge</string>
            </attr>
        </edge>
        <edge to="n3" from="n11">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n7" from="n11">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n5" from="n4">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n3" from="n4">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n4" from="n4">
            <attr name="label">
                <string>type:Bridge</string>
            </attr>
        </edge>
        <edge to="n7" from="n8">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n8" from="n8">
            <attr name="label">
                <string>type:Bridge</string>
            </attr>
        </edge>
        <edge to="n9" from="n8">
            <attr name="label">
                <string>end</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>type:Area</string>
            </attr>
        </edge>
        <edge to="n5" from="n5">
            <attr name="label">
                <string>even</string>
            </attr>
        </edge>
    </graph>
</gxl>
