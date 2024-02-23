<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph edgemode="directed" edgeids="false" role="graph" id="start">
        <attr name="$version">
            <string>curly</string>
        </attr>
        <attr name="$version">
            <string>curly</string>
        </attr>
        <node id="n1236">
            <attr name="layout">
                <string>357 108 39 15</string>
            </attr>
        </node>
        <node id="n1234">
            <attr name="layout">
                <string>532 301 38 15</string>
            </attr>
        </node>
        <node id="n1239">
            <attr name="layout">
                <string>535 210 38 15</string>
            </attr>
        </node>
        <node id="n1237">
            <attr name="layout">
                <string>532 142 38 15</string>
            </attr>
        </node>
        <node id="n1235">
            <attr name="layout">
                <string>364 251 39 15</string>
            </attr>
        </node>
        <node id="n1238">
            <attr name="layout">
                <string>94 150 28 15</string>
            </attr>
        </node>
        <edge to="n1239" from="n1239">
            <attr name="label">
                <string>Station</string>
            </attr>
        </edge>
        <edge to="n1234" from="n1235">
            <attr name="label">
                <string>dest</string>
            </attr>
        </edge>
        <edge to="n1239" from="n1236">
            <attr name="label">
                <string>dest</string>
            </attr>
        </edge>
        <edge to="n1238" from="n1238">
            <attr name="label">
                <string>Train</string>
            </attr>
        </edge>
        <edge to="n1237" from="n1236">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n1236" from="n1236">
            <attr name="label">
                <string>Person</string>
            </attr>
        </edge>
        <edge to="n1234" from="n1239">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n1239" from="n1238">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n1239" from="n1237">
            <attr name="label">
                <string>next</string>
            </attr>
        </edge>
        <edge to="n1235" from="n1235">
            <attr name="label">
                <string>Person</string>
            </attr>
        </edge>
        <edge to="n1237" from="n1235">
            <attr name="label">
                <string>at</string>
            </attr>
        </edge>
        <edge to="n1234" from="n1234">
            <attr name="label">
                <string>Station</string>
            </attr>
        </edge>
        <edge to="n1237" from="n1237">
            <attr name="label">
                <string>Station</string>
            </attr>
        </edge>
        <edge to="n1237" from="n1234">
            <attr name="label">
                <string>next</string>
            </attr>
            <attr name="layout">
                <string>500 0 580 306 641 149 580 149 14</string>
            </attr>
        </edge>
    </graph>
</gxl>
