<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start5">
        <node id="n98">
            <attr name="layout">
                <string>86 12 30 18</string>
            </attr>
        </node>
        <node id="n100">
            <attr name="layout">
                <string>19 119 30 18</string>
            </attr>
        </node>
        <node id="n101">
            <attr name="layout">
                <string>78 215 30 18</string>
            </attr>
        </node>
        <node id="n99">
            <attr name="layout">
                <string>144 122 40 18</string>
            </attr>
        </node>
        <node id="n102">
            <attr name="layout">
                <string>280 58 30 18</string>
            </attr>
        </node>
        <node id="n97">
            <attr name="layout">
                <string>249 205 30 18</string>
            </attr>
        </node>
        <edge to="n101" from="n101">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge to="n102" from="n98">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n99" from="n99">
            <attr name="label">
                <string>Buffer</string>
            </attr>
        </edge>
        <edge to="n100" from="n101">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n100" from="n100">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge to="n102" from="n99">
            <attr name="label">
                <string>first</string>
            </attr>
        </edge>
        <edge to="n97" from="n97">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge to="n101" from="n97">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n98" from="n99">
            <attr name="label">
                <string>last</string>
            </attr>
        </edge>
        <edge to="n97" from="n102">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n98" from="n100">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n102" from="n102">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
        <edge to="n98" from="n98">
            <attr name="label">
                <string>Cell</string>
            </attr>
        </edge>
    </graph>
</gxl>
